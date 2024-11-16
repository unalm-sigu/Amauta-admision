const ProgramacionNivelacion = httpVueLoader('/app/nivelacioneegg/programacionnivelacion/ProgramacionNivelacionInicio.vue');

new Vue({
    el: '#firstVue',
    components: {ProgramacionNivelacion},
    template: "<programacion-nivelacion></programacion-nivelacion>"
});