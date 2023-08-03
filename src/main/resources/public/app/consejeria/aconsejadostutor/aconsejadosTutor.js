const AconsejadosTutor = httpVueLoader('/app/consejeria/aconsejadostutor/AconsejadosTutorInicio.vue');

new Vue({
    el: '#firstVue',
    components: {AconsejadosTutor},
    template: "<aconsejados-tutor></aconsejados-tutor>"
});