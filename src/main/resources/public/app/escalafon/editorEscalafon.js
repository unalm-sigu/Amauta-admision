const package = '/app/escalafon';


const EditorEscalafon = httpVueLoader(package + '/EditorEscalafon.vue');

const GeneralEscalafon = httpVueLoader(package + '/general/GeneralEscalafon.vue');
const IdiomaEscalafon = httpVueLoader(package + '/idioma/IdiomaEscalafon.vue');
const InvestigacionEscalafon = httpVueLoader(package + '/investigacion/InvestigacionEscalafon.vue');


const router = new VueRouter({
    routes: [
        {
            name: 'editor', path: '/', redirect: '/idioma', component: EditorEscalafon,
            children: [
                {name: 'general', path: 'general', component: GeneralEscalafon},
                {name: 'idioma', path: 'idioma', component: IdiomaEscalafon},
                {name: 'investigacion', path: 'investigacion', component: InvestigacionEscalafon},
            ]
        }
    ]
});


const CONFIG_DATE = {format: 'DD/MM/YYYY', locale: 'es', showClear: true, showClose: true};


new Vue({
    el: '#escalafonvue',
    router
})


