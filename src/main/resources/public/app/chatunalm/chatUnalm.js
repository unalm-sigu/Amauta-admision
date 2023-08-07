const ChatUnalm = httpVueLoader('/app/chatunalm/ChatUnalmInicio.vue');

new Vue({
    el: '#chatVue',
    components: {ChatUnalm},
    template: "<chat-unalm></chat-unalm>"
});