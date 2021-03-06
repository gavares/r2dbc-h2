/*
 * Copyright 2017-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.r2dbc.h2;

import org.h2.result.ResultInterface;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class H2RowMetadataTest {

    private final List<H2ColumnMetadata> columnMetadatas = Arrays.asList(
        new H2ColumnMetadata("test-name-1", (long) 100, 200),
        new H2ColumnMetadata("test-name-2", (long) 300, 400)
    );

    private final ResultInterface result = mock(ResultInterface.class, RETURNS_SMART_NULLS);

    @Test
    void constructorNoColumnMetadata() {
        assertThatNullPointerException().isThrownBy(() -> new H2RowMetadata(null))
            .withMessage("columnMetadatas must not be null");
    }

    @Test
    void getColumnMetadataIndex() {
        assertThat(new H2RowMetadata(this.columnMetadatas).getColumnMetadata(1))
            .isEqualTo(new H2ColumnMetadata("test-name-2", (long) 300, 400));
    }

    @Test
    void getColumnMetadataInvalidIndex() {
        assertThatIllegalArgumentException().isThrownBy(() -> new H2RowMetadata(this.columnMetadatas).getColumnMetadata(2))
            .withMessage("Column index 2 is larger than the number of columns 2");
    }

    @Test
    void getColumnMetadataInvalidName() {
        assertThatIllegalArgumentException().isThrownBy(() -> new H2RowMetadata(this.columnMetadatas).getColumnMetadata("test-name-3"))
            .withMessage("Column name 'test-name-3' does not exist in column names [test-name-1, test-name-2]");
    }

    @Test
    void getColumnMetadataName() {
        assertThat(new H2RowMetadata(this.columnMetadatas).getColumnMetadata("test-name-2"))
            .isEqualTo(new H2ColumnMetadata("test-name-2", (long) 300, 400));
    }

    @Test
    void getColumnMetadataNoIdentifier() {
        assertThatNullPointerException().isThrownBy(() -> new H2RowMetadata(this.columnMetadatas).getColumnMetadata(null))
            .withMessage("identifier must not be null");
    }

    @Test
    void getColumnMetadataWrongIdentifierType() {
        Object identifier = new Object();

        assertThatIllegalArgumentException().isThrownBy(() -> new H2RowMetadata(this.columnMetadatas).getColumnMetadata(identifier))
            .withMessage("Identifier '%s' is not a valid identifier. Should either be an Integer index or a String column name.", identifier.toString());
    }

    @Test
    void getColumnMetadatas() {
        assertThat(new H2RowMetadata(this.columnMetadatas).getColumnMetadatas()).containsAll(this.columnMetadatas);
    }

    @Test
    void toRowMetadata() {
        when(this.result.getVisibleColumnCount()).thenReturn(1);
        when(this.result.getColumnName(0)).thenReturn("test-name");
        when(this.result.getColumnPrecision(0)).thenReturn(400L);
        when(this.result.getColumnType(0)).thenReturn(200);

        H2RowMetadata rowMetadata = H2RowMetadata.toRowMetadata(this.result);

        assertThat(rowMetadata.getColumnMetadatas()).hasSize(1);
    }

    @Test
    void toRowMetadataNoResult() {
        assertThatNullPointerException().isThrownBy(() -> H2RowMetadata.toRowMetadata(null))
            .withMessage("result must not be null");
    }
}