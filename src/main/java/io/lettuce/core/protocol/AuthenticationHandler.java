package io.lettuce.core.protocol;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.AttributeKey;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

/**
 * Handler to initialize a Redis Connection using a {@link ConnectionInitializer}.
 *
 * @author Mark Paluch
 * @since 6.0
 */
public class AuthenticationHandler extends ChannelDuplexHandler {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AuthenticationHandler.class);

    public static final AttributeKey<Boolean> IN_TX = AttributeKey.valueOf("IN_TX");

    // Store latest deferred AUTH command
    private RedisCommand<?, ?, ?> deferredAuthCmd;

    public AuthenticationHandler() {

    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        if (msg instanceof RedisCommand) {
            RedisCommand<?, ?, ?> command = (RedisCommand<?, ?, ?>) msg;
            if (TxAwareCommand.isTxAwareCommand(command)) {
                if (inTx(ctx)) {
                    if (deferredAuthCmd != null) {
                        //newer credentials pushed
                        deferredAuthCmd.complete();
                    }

                    deferredAuthCmd = command;  // Store the latest AUTH command
                    promise.trySuccess();
                    return;
                }
            }
        }

        ctx.write(msg, promise);
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof CommandHandler.TxEnded) {
            replayDeferred(ctx);
        }
        super.userEventTriggered(ctx, evt);
    }

    private Boolean inTx(ChannelHandlerContext ctx) {
        if (ctx.channel().attr(IN_TX).get() != null) {
            return ctx.channel().attr(IN_TX).get();
        }

        return false;
    }

    private void replayDeferred(ChannelHandlerContext ctx) {
        if (deferredAuthCmd != null) {
            ctx.channel().pipeline().writeAndFlush(deferredAuthCmd);
            deferredAuthCmd = null;  // Clear the deferred command after flushing
        }
    }
}
