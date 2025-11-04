import React, {useEffect, useState} from 'react'
import {Button} from 'primereact/button'
import {InputText} from 'primereact/inputtext'
import {InputNumber} from 'primereact/inputnumber'
import {Dropdown} from 'primereact/dropdown'
import {TabPanel, TabView} from 'primereact/tabview'
import {classNames} from 'primereact/utils'
import PersonService from "../../services/PersonService.jsx";
import {MOVIE_GENRES, MPAA_RATINGS} from "../../domain/values.js";
import {validateMovieForm} from "../../utils/validation.js";

const MovieForm = ({movie, onSubmit, onCancel, loading}) => {
    const [formData, setFormData] = useState({
        name: '',
        oscarsCount: 0,
        budget: 0,
        totalBoxOffice: 0,
        mpaaRating: null,
        directorReference: { id: null, value: null },
        screenwriterReference: { id: null, value: null },
        operatorReference: { id: null, value: null },
        length: null,
        goldenPalmCount: 0,
        usaBoxOffice: 0,
        tagline: '',
        genre: null,
        coordinates: {x: 0, y: 0}
    })

    const [errors, setErrors] = useState({})

    // Состояния для персон
    const [directors, setDirectors] = useState([])
    const [screenwriters, setScreenwriters] = useState([])
    const [operators, setOperators] = useState([])

    // Пагинация
    const [directorTotal, setDirectorTotal] = useState(0)
    const [screenwriterTotal, setScreenwriterTotal] = useState(0)
    const [operatorTotal, setOperatorTotal] = useState(0)

    useEffect(() => {
        if (movie) {
            setFormData({
                name: movie.name || '',
                oscarsCount: movie.oscarsCount || 0,
                budget: movie.budget || 0,
                totalBoxOffice: movie.totalBoxOffice || 0,
                mpaaRating: movie.mpaaRating || null,
                directorReference: movie.directorReference || {
                    id: movie.director?.id ?? null,
                    value: null
                },
                screenwriterReference: movie.screenwriterReference || {
                    id: movie.screenwriter?.id ?? null,
                    value: null
                },
                operatorReference: movie.operatorReference || {
                    id: movie.operator?.id ?? null,
                    value: null
                },
                length: movie.length || null,
                goldenPalmCount: movie.goldenPalmCount || 0,
                usaBoxOffice: movie.usaBoxOffice || 0,
                tagline: movie.tagline || '',
                genre: movie.genre || null,
                coordinates: movie.coordinates || {x: 0, y: 0}
            })
        }
    }, [movie])

    const loadPersons = async (personType = 'all', first = 0, rows = 10) => {
        try {
            const response = await PersonService.getPeople({
                first: first,
                rows: rows,
                sortField: null,
                sortOrder: null,
                filters: {}
            })

            const people = {
                data: response.data.data,
                total: response.data.totalRecords
            }

            switch (personType) {
                case 'directors':
                    setDirectors(people.data)
                    setDirectorTotal(people.total)
                    break
                case 'screenwriters':
                    setScreenwriters(people.data)
                    setScreenwriterTotal(people.total)
                    break
                case 'operators':
                    setOperators(people.data)
                    setOperatorTotal(people.total)
                    break
                case 'all':
                default:
                    setDirectors(people.data)
                    setScreenwriters(people.data)
                    setOperators(people.data)
                    setDirectorTotal(people.total)
                    setScreenwriterTotal(people.total)
                    setOperatorTotal(people.total)
            }
        } catch (error) {
            console.error('Ошибка загрузки персон:', error)
        }
    }

    const handleLazyLoad = (personType, event) => {
        const {first, rows} = event

        switch (personType) {
            case 'directors':
                loadPersons('directors', first, rows)
                break
            case 'screenwriters':
                loadPersons('screenwriters', first, rows)
                break
            case 'operators':
                loadPersons('operators', first, rows)
                break
        }
    }

    const validateForm = () => {
        const newErrors = validateMovieForm(formData)
        setErrors(newErrors)
        return Object.keys(newErrors).length === 0
    }

    const handleSubmit = (e) => {
        e.preventDefault()
        if (validateForm()) {
            onSubmit(formData)
        }
    }

    const handleInputChange = (field, value) => {
        setFormData(prev => ({...prev, [field]: value}))
    }

    const handleReferenceChange = (field, id) => {
        setFormData(prev => ({
            ...prev,
            [field]: {
                id,
                value: null
            }
        }))
    }

    const handleCoordinatesChange = (field, value) => {
        setFormData(prev => ({
            ...prev,
            coordinates: {...prev.coordinates, [field]: value}
        }))
    }

    return (
        <form onSubmit={handleSubmit}>
            <TabView>
                <TabPanel header="Основная информация">
                    <div className="form-field">
                        <label>Название *</label>
                        <InputText
                            value={formData.name}
                            onChange={(e) => handleInputChange('name', e.target.value)}
                            className={classNames({'p-invalid': errors.name})}
                            style={{width: '100%'}}
                        />
                        {errors.name && <small className="p-error">{errors.name}</small>}
                    </div>

                    <div className="form-field">
                        <label>Количество Оскаров *</label>
                        <InputNumber
                            value={formData.oscarsCount}
                            onValueChange={(e) => handleInputChange('oscarsCount', e.value)}
                            className={classNames({'p-invalid': errors.oscarsCount})}
                            min={1}
                            style={{width: '100%'}}
                        />
                        {errors.oscarsCount && <small className="p-error">{errors.oscarsCount}</small>}
                    </div>

                    <div className="form-field">
                        <label>Бюджет *</label>
                        <InputNumber
                            value={formData.budget}
                            onValueChange={(e) => handleInputChange('budget', e.value)}
                            className={classNames({'p-invalid': errors.budget})}
                            min={0.01}
                            mode="currency"
                            currency="USD"
                            locale="en-US"
                            style={{width: '100%'}}
                        />
                        {errors.budget && <small className="p-error">{errors.budget}</small>}
                    </div>

                    <div className="form-field">
                        <label>Сборы *</label>
                        <InputNumber
                            value={formData.totalBoxOffice}
                            onValueChange={(e) => handleInputChange('totalBoxOffice', e.value)}
                            className={classNames({'p-invalid': errors.totalBoxOffice})}
                            min={1}
                            mode="currency"
                            currency="USD"
                            locale="en-US"
                            maxFractionDigits={0}
                            style={{width: '100%'}}
                        />
                        {errors.totalBoxOffice && <small className="p-error">{errors.totalBoxOffice}</small>}
                    </div>

                    <div className="form-field">
                        <label>Сборы в США *</label>
                        <InputNumber
                            value={formData.usaBoxOffice}
                            onValueChange={(e) => handleInputChange('usaBoxOffice', e.value)}
                            className={classNames({'p-invalid': errors.usaBoxOffice})}
                            min={1}
                            mode="currency"
                            currency="USD"
                            locale="en-US"
                            maxFractionDigits={0}
                            style={{width: '100%'}}
                        />
                        {errors.usaBoxOffice && <small className="p-error">{errors.usaBoxOffice}</small>}
                    </div>

                    <div className="form-field">
                        <label>Рейтинг MPAA</label>
                        <Dropdown
                            value={formData.mpaaRating}
                            options={MPAA_RATINGS}
                            onChange={(e) => handleInputChange('mpaaRating', e.value)}
                            placeholder="Выберите рейтинг"
                            style={{width: '100%'}}
                        />
                    </div>

                    <div className="form-field">
                        <label>Жанр</label>
                        <Dropdown
                            value={formData.genre}
                            options={MOVIE_GENRES}
                            onChange={(e) => handleInputChange('genre', e.value)}
                            placeholder="Выберите жанр"
                            style={{width: '100%'}}
                        />
                    </div>

                    <div className="form-field">
                        <label>Длина</label>
                        <InputNumber
                            value={formData.length}
                            onValueChange={(e) => handleInputChange('length', e.value)}
                            min={1}
                            style={{width: '100%'}}
                        />
                    </div>

                    <div className="form-field">
                        <label>Количество Золотых пальм *</label>
                        <InputNumber
                            value={formData.goldenPalmCount}
                            onValueChange={(e) => handleInputChange('goldenPalmCount', e.value)}
                            className={classNames({'p-invalid': errors.goldenPalmCount})}
                            min={1}
                            style={{width: '100%'}}
                        />
                        {errors.goldenPalmCount && <small className="p-error">{errors.goldenPalmCount}</small>}
                    </div>

                    <div className="form-field">
                        <label>Слоган *</label>
                        <InputText
                            value={formData.tagline}
                            onChange={(e) => handleInputChange('tagline', e.target.value)}
                            className={classNames({'p-invalid': errors.tagline})}
                            style={{width: '100%'}}
                        />
                        {errors.tagline && <small className="p-error">{errors.tagline}</small>}
                    </div>
                </TabPanel>

                <TabPanel header="Координаты">
                    <div className="form-field">
                        <label>X *</label>
                        <InputNumber
                            value={formData.coordinates?.x || 0}
                            onValueChange={(e) => handleCoordinatesChange('x', e.value)}
                            className={classNames({'p-invalid': errors.coordinatesX})}
                            min={-737}
                            style={{width: '100%'}}
                        />
                        {errors.coordinatesX && <small className="p-error">{errors.coordinatesX}</small>}
                    </div>

                    <div className="form-field">
                        <label>Y *</label>
                        <InputNumber
                            value={formData.coordinates?.y || 0}
                            onValueChange={(e) => handleCoordinatesChange('y', e.value)}
                            className={classNames({'p-invalid': errors.coordinatesY})}
                            max={462}
                            style={{width: '100%'}}
                        />
                        {errors.coordinatesY && <small className="p-error">{errors.coordinatesY}</small>}
                    </div>
                </TabPanel>

                <TabPanel header="Персоны">
                    <div className="form-field">
                        <label>Режиссер *</label>
                        <Dropdown
                            value={formData.directorReference?.id ?? null}
                            options={directors}
                            onChange={(e) => handleReferenceChange('directorReference', e.value)}
                            optionLabel="name"
                            optionValue="id"
                            placeholder="Выберите режиссера"
                            className={classNames({'p-invalid': errors.directorReference})}
                            style={{width: '100%'}}
                            virtualScrollerOptions={{
                                itemSize: 38,
                                lazy: true,
                                onLazyLoad: (e) => handleLazyLoad('directors', e),
                                options: {
                                    totalRecords: directorTotal
                                }
                            }}
                        />
                        {errors.directorReference && <small className="p-error">{errors.directorReference}</small>}
                    </div>

                    <div className="form-field">
                        <label>Сценарист</label>
                        <Dropdown
                            value={formData.screenwriterReference?.id ?? null}
                            options={screenwriters}
                            onChange={(e) => handleReferenceChange('screenwriterReference', e.value)}
                            optionLabel="name"
                            optionValue="id"
                            placeholder="Выберите сценариста"
                            style={{width: '100%'}}
                            virtualScrollerOptions={{
                                itemSize: 38,
                                lazy: true,
                                onLazyLoad: (e) => handleLazyLoad('screenwriters', e),
                                options: {
                                    totalRecords: screenwriterTotal
                                }
                            }}
                        />
                    </div>

                    <div className="form-field">
                        <label>Оператор</label>
                        <Dropdown
                            value={formData.operatorReference?.id ?? null}
                            options={operators}
                            onChange={(e) => handleReferenceChange('operatorReference', e.value)}
                            optionLabel="name"
                            optionValue="id"
                            placeholder="Выберите оператора"
                            style={{width: '100%'}}
                            virtualScrollerOptions={{
                                itemSize: 38,
                                lazy: true,
                                onLazyLoad: (e) => handleLazyLoad('operators', e),
                                options: {
                                    totalRecords: operatorTotal
                                }
                            }}
                        />
                    </div>
                </TabPanel>
            </TabView>

            <div className="form-field mt-3">
                <Button type="submit" label="Сохранить" loading={loading} className="mr-2"/>
                <Button type="button" label="Отмена" onClick={onCancel} className="p-button-secondary"/>
            </div>
        </form>
    )
}

export default MovieForm