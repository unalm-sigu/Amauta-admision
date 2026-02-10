const GrupoNivelacion = httpVueLoader('/app/nivelacioneegg/gruponivelacion/GrupoNivelacion.vue');

new Vue({
    el: '#firstVue',
    components: {GrupoNivelacion},
    template: "<grupo-nivelacion></grupo-nivelacion>"
});