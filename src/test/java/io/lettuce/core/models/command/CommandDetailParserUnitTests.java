/*
 * Copyright 2011-Present, Redis Ltd. and Contributors
 * All rights reserved.
 *
 * Licensed under the MIT License.
 *
 * This file contains contributions from third-party contributors
 * licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.lettuce.core.models.command;

import static io.lettuce.TestTags.UNIT_TEST;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.lettuce.core.internal.LettuceLists;

/**
 * Unit tests for {@link CommandDetailParser}.
 *
 * @author Mark Paluch
 * @author Mikhael Sokolov
 */
@Tag(UNIT_TEST)
class CommandDetailParserUnitTests {

    @Test
    void testMappings() {
        assertThat(CommandDetailParser.FLAG_MAPPING).hasSameSizeAs(CommandDetail.Flag.values());
    }

    @Test
    void testEmptyList() {

        List<CommandDetail> result = CommandDetailParser.parse(new ArrayList<>());
        assertThat(result).isEmpty();
    }

    @Test
    void testMalformedList() {
        Object o = LettuceLists.newList("", "", "");
        List<CommandDetail> result = CommandDetailParser.parse(LettuceLists.newList(o));
        assertThat(result).isEmpty();
    }

    @Test
    void testParse() {
        Object o = LettuceLists.newList("get", "1", LettuceLists.newList("fast", "loading"), 1L, 2L, 3L,
                LettuceLists.newList("@read", "@string", "@fast"));
        List<CommandDetail> result = CommandDetailParser.parse(LettuceLists.newList(o));
        assertThat(result).hasSize(1);

        CommandDetail commandDetail = result.get(0);
        assertThat(commandDetail.getName()).isEqualTo("get");
        assertThat(commandDetail.getArity()).isEqualTo(1);
        assertThat(commandDetail.getFlags()).hasSize(2);
        assertThat(commandDetail.getFirstKeyPosition()).isEqualTo(1);
        assertThat(commandDetail.getLastKeyPosition()).isEqualTo(2);
        assertThat(commandDetail.getKeyStepCount()).isEqualTo(3);
        assertThat(commandDetail.getAclCategories()).hasSize(3);
    }

    @Test
    void testModel() {
        CommandDetail commandDetail = new CommandDetail();
        commandDetail.setArity(1);
        commandDetail.setFirstKeyPosition(2);
        commandDetail.setLastKeyPosition(3);
        commandDetail.setKeyStepCount(4);
        commandDetail.setName("theName");
        commandDetail.setFlags(new HashSet<>());
        commandDetail.setAclCategories(new HashSet<>());

        assertThat(commandDetail.toString()).contains(CommandDetail.class.getSimpleName());
    }

}
