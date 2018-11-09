Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#main',
    data: {
        URL: APP.url('rolexamen/gruporegular'),
        rolesExamenes: JSON.parse(jRolesExamenes),
        rolExamen: null
    },
    mounted() {
        console.dir(this.rolesExamenes);
    },
    methods: {
        rolExamenCustomLabel( { eventoCicloAcademico }) {
            if (eventoCicloAcademico == null || eventoCicloAcademico.eventoAcademico == null) {
                return "";
            }
            return `${eventoCicloAcademico.eventoAcademico.nombre}`;
        }, calcularGrupoRegular() {
            $('#frmCalcular').find(".multiselect__input").each(function () {
                $(this).attr("required", true);
            });

            $('#frmCalcular').find('.multiselect__input').each(function () {
                var input = $(this);
                let element = input.closest('.multiselect').find('.multiselect__single');

                if (element.css('display') != 'none' && element.html() != "") {
                    $(this).removeAttr("required");
                }
            });

            var form = $("[id='frmCalcular']");
            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }
            AXIOS.post(`${this.URL}/calcularGruposRegulares`, this.rolExamen)
                    .then(response => {
                        if (response.data.success) {

                            notify(response.data.message, 'info');
                        } else {
                            notify(response.data.message, 'error');
                        }
                    })

        }
    }
});
