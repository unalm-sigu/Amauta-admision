const CargaNivelacion = httpVueLoader('/app/nivelacioneegg/carganivelacion/CargaNivelacionInicio.vue');

new Vue({
    el: '#firstVue',
    components: {CargaNivelacion},
    template: "<carga-nivelacion></carga-nivelacion>"
});