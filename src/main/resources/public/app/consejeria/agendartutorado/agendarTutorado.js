const AgendarTutorado = httpVueLoader('/app/consejeria/agendartutorado/AgendarTutoradoInicio.vue');

new Vue({
    el: '#firstVue',
    components: {AgendarTutorado},
    template: "<agendar-tutorado></agendar-tutorado>"
});