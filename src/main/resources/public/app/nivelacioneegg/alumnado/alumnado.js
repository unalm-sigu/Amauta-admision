const AlumnadoInicio = httpVueLoader('/app/nivelacioneegg/alumnado/AlumnadoInicio.vue');

new Vue({
    el: '#firstVue',
    components: {AlumnadoInicio},
    template: "<alumnado-inicio></alumnado-inicio>"
});