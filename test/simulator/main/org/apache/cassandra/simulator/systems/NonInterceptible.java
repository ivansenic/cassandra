/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.cassandra.simulator.systems;

import java.util.function.Supplier;

import org.apache.cassandra.utils.Shared;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.apache.cassandra.utils.Shared.Scope.SIMULATION;

@Shared(scope = SIMULATION)
public class NonInterceptible
{
    private static final ThreadLocal<Boolean> PERMIT = new ThreadLocal<>();

    public static boolean isPermitted()
    {
        return TRUE.equals(PERMIT.get());
    }

    public static void execute(Runnable runnable)
    {
        if (isPermitted())
        {
            runnable.run();
        }
        else
        {
            PERMIT.set(TRUE);
            try
            {
                runnable.run();
            }
            finally
            {
                PERMIT.set(FALSE);
            }
        }
    }

    public static <V> V apply(Supplier<V> supplier)
    {
        if (isPermitted())
        {
            return supplier.get();
        }
        else
        {
            PERMIT.set(TRUE);
            try
            {
                return supplier.get();
            }
            finally
            {
                PERMIT.set(FALSE);
            }
        }
    }
}
