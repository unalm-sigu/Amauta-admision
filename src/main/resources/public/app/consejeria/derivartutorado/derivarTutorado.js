const DerivarTutorado = httpVueLoader('/app/consejeria/derivartutorado/DerivarTutoradoInicio.vue');

new Vue({
    el: '#firstVue',
    components: {DerivarTutorado},
    template: "<derivar-tutorado></derivar-tutorado>"
});