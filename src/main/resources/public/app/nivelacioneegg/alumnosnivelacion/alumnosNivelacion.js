const AlumnosNivelacion = httpVueLoader('/app/nivelacioneegg/alumnosnivelacion/AlumnosNivelacionInicio.vue');

new Vue({
    el: '#firstVue',
    components: {AlumnosNivelacion},
    template: "<alumnos-nivelacion></alumnos-nivelacion>"
});