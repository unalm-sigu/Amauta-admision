const CursoReplicaNivelacion = httpVueLoader('/app/nivelacioneegg/cursoreplicanivelacion/CursoReplicaNivelacion.vue');

new Vue({
    el: '#firstVue',
    components: {CursoReplicaNivelacion},
    template: "<curso-replica-nivelacion></curso-replica-nivelacion>"
});