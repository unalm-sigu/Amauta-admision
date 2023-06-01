const PlanTutoria = httpVueLoader('/app/consejeria/plantutoria/PlanTutoriaInicio.vue');

new Vue({
    el: '#firstVue',
    components: {PlanTutoria},
    template: "<plan-tutoria></plan-tutoria>"
});