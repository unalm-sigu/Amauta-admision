const LeccionNivelacion = httpVueLoader('/app/nivelacioneegg/leccionnivelacion/LeccionNivelacionInicio.vue');

new Vue({
    el: '#firstVue',
    components: {LeccionNivelacion},
    template: "<leccion-nivelacion></leccion-nivelacion>"
});