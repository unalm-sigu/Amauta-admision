const InfoAcademico = httpVueLoader('/app/academico/pronabec/InfoAcademicoInicioBecado.vue');

new Vue({
    el: '#firstVue',
    components: {InfoAcademico},
    template: "<info-academico></info-academico>"
});