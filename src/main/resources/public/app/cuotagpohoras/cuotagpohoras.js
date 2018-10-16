Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#cuotagpohorasVUE',
    data: {
        cuotagpohorasURL: APP.url('academico/cuotagpohoras/list')        
    },
    mounted() {
         $(".numerico").numeric({negative: false});
    },
    methods: {       
    }
});