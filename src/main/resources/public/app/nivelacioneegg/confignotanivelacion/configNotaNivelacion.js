const ConfigNotaNivelacion = httpVueLoader('/app/nivelacioneegg/confignotanivelacion/ConfigNotaNivelacionInicio.vue');

new Vue({
    el: '#firstVue',
    components: {ConfigNotaNivelacion},
    template: "<config-nota-nivelacion></config-nota-nivelacion>"
});