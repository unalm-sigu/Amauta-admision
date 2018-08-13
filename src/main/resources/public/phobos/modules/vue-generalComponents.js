Vue.component("format-date", {
    template: `<span v-text='formatedDate'></span>`,
    props: {
        datetext: {
            required: true
        },
        formatinput: {
            default: 'DD/MM/YYYY hh:mm:ss',
            required: false
        },
        formatoutput: {
            default: 'DD/MM/YYYY', //
            required: false
        }
    },
    data: function () {
        return {
            formatedDate: ''
        }
    },
    mounted: function () {
        //   var date = moment(this.datetext).format('DD/MM/YYYY', 'hh:mm:ss');
        if (this.datetext != null && this.datetext != "") {
            let momentDate = moment(this.datetext, this.formatinput);
            this.formatedDate = momentDate.format(this.formatoutput);
        }
    },
    destroyed: function () {

    },
    watch: function () {

    }
});