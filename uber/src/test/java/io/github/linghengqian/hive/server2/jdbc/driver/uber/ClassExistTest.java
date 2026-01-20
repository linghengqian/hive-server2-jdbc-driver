/*
 * Copyright 2026 Qiheng He
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.linghengqian.hive.server2.jdbc.driver.uber;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ClassExistTest {

    @Test
    void test() {
        assertDoesNotThrow(() -> {
            Class.forName("org.apache.hadoop.hive.conf.HiveConf");
            Class.forName("org.apache.hive.org.apache.commons.text.similarity.CosineDistance");
            Class.forName("org.apache.hive.org.apache.commons.logging.LogFactory");
            Class.forName("org.apache.hive.com.google.common.base.MoreObjects");
            Class.forName("org.apache.hive.org.apache.commons.codec.binary.Base16");
            Class.forName("org.apache.hive.org.codehaus.stax2.AttributeInfo");
            Class.forName("org.apache.hive.org.apache.thrift.transport.TSSLTransportFactory$TSSLTransportParameters");
            Class.forName("org.apache.hive.org.apache.zookeeper.KeeperException");
            Class.forName("org.apache.hive.org.apache.http.Consts");
        });
        assertThrows(ClassNotFoundException.class, () -> Class.forName("org.apache.hadoop.mapred.JobConf"));
        assertThrows(ClassNotFoundException.class, () -> Class.forName("org.apache.commons.text.similarity.CosineDistance"));
        assertThrows(ClassNotFoundException.class, () -> Class.forName("org.apache.commons.logging.LogFactory"));
        assertThrows(ClassNotFoundException.class, () -> Class.forName("com.google.common.base.MoreObjects"));
        assertThrows(ClassNotFoundException.class, () -> Class.forName("org.apache.commons.codec.binary.Base16"));
        assertThrows(ClassNotFoundException.class, () -> Class.forName("org.codehaus.stax2.AttributeInfo"));
        assertThrows(ClassNotFoundException.class, () -> Class.forName("org.apache.thrift.transport.TSSLTransportFactory$TSSLTransportParameters"));
        assertThrows(ClassNotFoundException.class, () -> Class.forName("org.apache.zookeeper.KeeperException"));
        assertThrows(ClassNotFoundException.class, () -> Class.forName("org.apache.http.Consts"));
    }
}
