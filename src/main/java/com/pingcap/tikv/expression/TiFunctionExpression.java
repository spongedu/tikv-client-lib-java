/*
 * Copyright 2017 PingCAP, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pingcap.tikv.expression;


import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableList;
import com.pingcap.tidb.tipb.Expr;
import com.pingcap.tidb.tipb.ExprType;
import com.pingcap.tikv.util.TiFluentIterable;

import java.util.List;
import java.util.Objects;

public abstract class TiFunctionExpression implements TiExpr {

    protected final List<TiExpr> args;

    protected TiFunctionExpression(TiExpr... args) {
        this.args = ImmutableList.copyOf(args);
        validateArguments(args);
    }

    protected abstract ExprType getExprType();

    public TiExpr getArg(int i) {
        checkArgument(i < args.size(), "Index out of bound for TiExpression Arguments");
        return args.get(i);
    }

    public int getArgSize() {
        return args.size();
    }

    @Override
    public Expr toProto() {
        Expr.Builder builder = Expr.newBuilder();

        builder.setTp(getExprType());
        builder.addAllChildren(TiFluentIterable
                .from(args)
                .transform(TiExpr::toProto)
        );

        return builder.build();
    }

    public abstract String getName();

    protected void validateArguments(TiExpr... args) throws RuntimeException {
        requireNonNull(args, "Expressions cannot be null");
        for (TiExpr expr : args) {
            requireNonNull(expr, "Expressions cannot be null.");
        }
    }
}
