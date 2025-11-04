import React, {useEffect, useRef} from 'react'
import {DataTable} from 'primereact/datatable'
import {Column} from 'primereact/column'
import {Dropdown} from "primereact/dropdown";
import {COLORS, COUNTRIES} from "../../domain/values.js";
import {WS_BASE_PATH} from "../../config.js";

const PersonTable = ({
                         people,
                         loading,
                         lazyState,
                         setLazyState,
                         totalRecords,
                         selection,
                         onSelectionChange
                     }) => {

    const ws = useRef(null);

    useEffect(() => {
        ws.current = new WebSocket(`${WS_BASE_PATH}`);

        ws.current.onopen = () => {
            console.log('WebSocket connected for person');
        };

        ws.current.onmessage = (event) => {
            const data = JSON.parse(event.data);
            if (data.type === 'PERSON') {
                setLazyState(prev => ({...prev}));
            }
        };

        ws.current.onerror = (error) => {
            console.error('WebSocket error:', error);
        };

        ws.current.onclose = () => {
            console.log('WebSocket disconnected for person');
        };

        return () => {
            if (ws.current) {
                ws.current.close();
            }
        };
    }, []);

    const onPage = (event) => {
        setLazyState(prev => ({
            ...prev,
            first: event.first,
            rows: event.rows,
            page: event.page
        }));
    };

    const onSort = (event) => {
        setLazyState(prev => ({
            ...prev,
            sortField: event.sortField,
            sortOrder: event.sortOrder
        }));
    };

    const onFilter = (event) => {
        event['first'] = 0;
        setLazyState(prev => ({
            ...prev,
            filters: event.filters,
            first: 0,
            page: 0
        }));
    };

    const colorsFilterElement = (options) => {
        return (
            <Dropdown
                value={options.value}
                options={COLORS}
                onChange={(e) => options.filterApplyCallback(e.value)}
                placeholder="Все"
                className="p-column-filter"
                showClear
            />
        );
    }

    const countriesFilterElement = (options) => {
        return (
            <Dropdown
                value={options.value}
                options={COUNTRIES}
                onChange={(e) => options.filterApplyCallback(e.value)}
                placeholder="Все"
                className="p-column-filter"
                showClear
            />
        );
    }

    return (
        <div>
            <DataTable
                value={people}
                lazy
                dataKey="id"
                first={lazyState.first}
                sortField={lazyState.sortField}
                sortOrder={lazyState.sortOrder}
                onFilter={onFilter}
                onPage={onPage}
                onSort={onSort}
                loading={loading}
                removableSort
                paginator
                rows={lazyState.rows}
                rowsPerPageOptions={[5, 10, 25, 50]}
                totalRecords={totalRecords}
                filterDisplay="row"
                emptyMessage="Люди не найдены"
                selectionMode="single"
                selection={selection}
                onSelectionChange={onSelectionChange}
            >
                <Column
                    field="name"
                    header="Имя"
                    sortable
                    filter
                    filterMatchMode="contains"
                    showFilterMenu={false}
                    showClearButton={false}
                />
                <Column
                    field="eyeColor"
                    header="Цвет глаз"
                    sortable
                    filterElement={colorsFilterElement}
                    filter
                    filterMatchMode="contains"
                    showFilterMenu={false}
                    showClearButton={false}
                />
                <Column
                    field="hairColor"
                    header="Цвет волос"
                    sortable
                    filterElement={colorsFilterElement}
                    filter
                    filterMatchMode="contains"
                    showFilterMenu={false}
                    showClearButton={false}
                />
                <Column
                    field="nationality"
                    header="Национальность"
                    sortable
                    filterElement={countriesFilterElement}
                    filter
                    filterMatchMode="contains"
                    showFilterMenu={false}
                    showClearButton={false}
                />
                <Column
                    field="weight"
                    header="Вес"
                    sortable
                />
                <Column
                    field="location.x"
                    header="X"
                />
                <Column
                    field="location.y"
                    header="Y"
                />
                <Column
                    field="location.z"
                    header="Z"
                />
            </DataTable>
        </div>
    )
}

export default PersonTable