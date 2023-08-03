const InfoAcademico = httpVueLoader('/app/academico/alumno/InfoAcademicoInicio.vue');

new Vue({
    el: '#firstVue',
    components: {InfoAcademico},
    template: "<info-academico></info-academico>"
});