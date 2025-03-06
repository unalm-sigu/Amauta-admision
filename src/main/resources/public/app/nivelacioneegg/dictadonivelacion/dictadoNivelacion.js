const DictadoNivelacion = httpVueLoader('/app/nivelacioneegg/dictadonivelacion/DictadoNivelacionInicio.vue');

new Vue({
    el: '#firstVue',
    components: {DictadoNivelacion},
    template: "<dictado-nivelacion></dictado-nivelacion>"
});