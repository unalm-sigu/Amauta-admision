const InformeFinal = httpVueLoader('/app/consejeria/informefinal/InformeFinalInicio.vue');

new Vue({
    el: '#firstVue',
    components: {InformeFinal},
    template: "<informe-final></informe-final>"
});