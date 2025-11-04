import React, {useEffect, useRef} from 'react'
import {DataTable} from 'primereact/datatable'
import {Column} from 'primereact/column'
import {format, parseISO} from "date-fns";
import {ru} from "date-fns/locale";
import {MOVIE_GENRES, MPAA_RATINGS} from "../../domain/values.js";
import {Dropdown} from "primereact/dropdown";
import {WS_BASE_PATH} from "../../config.js";

const MovieTable = ({
                        movies,
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
            console.log('WebSocket connected for movies');
        };

        ws.current.onmessage = (event) => {
            const data = JSON.parse(event.data);
            if (data.type === 'MOVIE') {
                setLazyState(prev => ({...prev}));
            }
        };

        ws.current.onerror = (error) => {
            console.error('WebSocket error:', error);
        };

        ws.current.onclose = () => {
            console.log('WebSocket disconnected for movies');
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

    const creationDateBodyTemplate = (rowData) => {
        const dateString = rowData.creationDate;
        if (!dateString) return 'N/A';

        try {
            const date = parseISO(dateString);
            // Форматируем, например, в "05.10.2023 14:30" или просто "14:30"
            return format(date, 'dd.MM.yyyy HH:mm', {locale: ru});
        } catch (error) {
            console.error('Ошибка парсинга даты:', error);
            return dateString;
        }
    };

    const mpaaRatingFilterElement = (options) => {
        return (
            <Dropdown
                value={options.value}
                options={MPAA_RATINGS}
                onChange={(e) => options.filterApplyCallback(e.value)}
                placeholder="Все"
                className="p-column-filter"
                showClear
            />
        );
    }

    const genreFilterElement = (options) => {
        return (
            <Dropdown
                value={options.value}
                options={MOVIE_GENRES}
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
                value={movies}
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
                emptyMessage="Фильмы не найдены"
                selectionMode="single"
                selection={selection}
                onSelectionChange={onSelectionChange}
            >
                <Column
                    field="id"
                    header="ID"
                    sortable
                    filter
                    filterMatchMode="contains"
                    showFilterMenu={false}
                    showClearButton={false}
                />
                <Column
                    field="name"
                    header="Название"
                    sortable
                    filter
                    filterMatchMode="contains"
                    showFilterMenu={false}
                    showClearButton={false}
                />
                <Column
                    field="genre"
                    header="Жанр"
                    sortable
                    filterElement={genreFilterElement}
                    filter
                    filterMatchMode="equals"
                    showFilterMenu={false}
                    showClearButton={false}
                />
                <Column
                    field="mpaaRating"
                    header="Рейтинг"
                    sortable
                    filterElement={mpaaRatingFilterElement}
                    filter
                    filterMatchMode="contains"
                    showFilterMenu={false}
                    showClearButton={false}
                />
                <Column
                    field="tagline"
                    header="Слоган"
                    sortable
                    filter
                    filterMatchMode="contains"
                    showFilterMenu={false}
                    showClearButton={false}
                />
                <Column
                    field="creationDate"
                    header="Дата создания"
                    sortable
                    body={creationDateBodyTemplate}
                />
                <Column
                    field="oscarsCount"
                    header="Оскары"
                    sortable
                />
                <Column
                    field="budget"
                    header="Бюджет"
                    sortable
                />
                <Column
                    field="totalBoxOffice"
                    header="Сборы"
                    sortable
                />
                <Column
                    field="usaBoxOffice"
                    header="Сборы в США"
                    sortable
                />
                <Column
                    field="director.name"
                    header="Режисер"
                />
                <Column
                    field="screenwriter.name"
                    header="Сценарист"
                />
                <Column
                    field="operator.name"
                    header="Оператор"
                />
                <Column
                    field="length"
                    header="Продолжительность"
                    sortable
                />
                <Column
                    field="goldenPalmCount"
                    header="Золотые пальмы"
                    sortable
                />
                <Column
                    field="coordinates.x"
                    header="X"
                />
                <Column
                    field="coordinates.y"
                    header="Y"
                />
            </DataTable>
        </div>
    )
}

export default MovieTable