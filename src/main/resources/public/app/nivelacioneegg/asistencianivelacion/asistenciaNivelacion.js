const AsistenciaNivelacion = httpVueLoader('/app/nivelacioneegg/asistencianivelacion/AsistenciaNivelacionInicio.vue');

new Vue({
    el: '#firstVue',
    components: {AsistenciaNivelacion},
    template: "<asistencia-nivelacion></asistencia-nivelacion>"
});