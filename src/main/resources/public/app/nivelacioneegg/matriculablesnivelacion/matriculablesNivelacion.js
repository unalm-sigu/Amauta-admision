const MatriculablesNivelacion = httpVueLoader('/app/nivelacioneegg/matriculablesnivelacion/MatriculablesNivelacionInicio.vue');

new Vue({
    el: '#firstVue',
    components: {MatriculablesNivelacion},
    template: "<matriculables-nivelacion></matriculables-nivelacion>"
});