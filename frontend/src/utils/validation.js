export const validateMovieForm = (movieForm) => {
    const errors = {}

    if (!movieForm.name || movieForm.name.trim() === '') {
        errors.name = 'Название обязательно'
    }

    if (movieForm.oscarsCount <= 0) {
        errors.oscarsCount = 'Количество Оскаров должно быть больше 0'
    }

    if (movieForm.budget <= 0) {
        errors.budget = 'Бюджет должен быть больше 0'
    }

    if (movieForm.totalBoxOffice <= 0) {
        errors.totalBoxOffice = 'Сборы должны быть больше 0'
    }

    if (movieForm.goldenPalmCount <= 0) {
        errors.goldenPalmCount = 'Количество Золотых пальм должно быть больше 0'
    }

    if (movieForm.usaBoxOffice <= 0) {
        errors.usaBoxOffice = 'Сборы в США должны быть больше 0'
    }

    if (!movieForm.tagline || movieForm.tagline.trim() === '') {
        errors.tagline = 'Слоган обязателен'
    }

    if (!movieForm.coordinates || movieForm.coordinates.x <= -738) {
        errors.coordinatesX = 'Координата X должна быть больше -738'
    }

    if (!movieForm.coordinates || movieForm.coordinates.y > 462) {
        errors.coordinatesY = 'Координата Y должна быть не больше 462'
    }

    if (!movieForm.directorReference || !movieForm.directorReference.id) {
        errors.directorReference = 'Режиссер обязателен'
    }

    return errors
}

export const validatePersonForm = (personForm) => {
    const errors = {}

    if (!personForm.name || personForm.name.trim() === '') {
        errors.name = 'Имя обязательно'
    }

    if (personForm.weight <= 0) {
        errors.weight = 'Вес должен быть больше 0'
    }

    if (!personForm.hairColor) {
        errors.hairColor = 'Цвет волос обязателен'
    }

    return errors
}