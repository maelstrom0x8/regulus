/*
 * Copyright (C) 2024 Emmanuel Godwin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.ceze.regulus.config;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.LayoutBase;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TestLoggingLayout extends LayoutBase<ILoggingEvent> {
    String prefix = null;
    boolean printThreadName = true;
    private final String applicationName = "regulus";

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setPrintThreadName(boolean printThreadName) {
        this.printThreadName = printThreadName;
    }

    public String doLayout(ILoggingEvent event) {
        StringBuffer sbuf = new StringBuffer(128);

        sbuf.append(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
        sbuf.append(" ");
        sbuf.append(event.getLevel());
        sbuf.append(" ");
        sbuf.append(ProcessHandle.current().pid());
        sbuf.append(" --- [").append(applicationName).append("] ");
        sbuf.append("[\t\t").append(event.getThreadName()).append("] ");
        sbuf.append(event.getLoggerName());
        sbuf.append(" : ");
        sbuf.append(event.getFormattedMessage());
        sbuf.append("\n");
        return sbuf.toString();
    }
}
