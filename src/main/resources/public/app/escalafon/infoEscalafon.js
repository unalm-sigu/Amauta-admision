Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#main',
    data: {
        escalafon: JSON.parse(escalafonJson)
    },
    mounted: function () {
    },
    methods: {
    }
});