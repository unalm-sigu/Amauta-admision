const CursoIdiomaEditar = httpVueLoader('/app/academico/curso/cursoidioma/cursoidiomaeditar.vue');

new Vue({
    el: '#main',
    components: {
        CursoIdiomaEditar: CursoIdiomaEditar,
    },
    data: {
        cursoURL: APP.url('academico/curso/idioma/list'),
    },
    methods: {

    }
});