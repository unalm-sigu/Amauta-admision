const RegistroBorradoAlumno = httpVueLoader('/app/academico/registroborrado/RegistroBorradoAlumno.vue');

new Vue({
    el: '#firstVue',
    components: {RegistroBorradoAlumno},
    template: "<registro-borrado-alumno></registro-borrado-alumno>"
});