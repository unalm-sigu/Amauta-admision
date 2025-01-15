<template>
    <modal-vik ref="modalCurso"
               v-bind="modalCurso"
               v-bind:okaction="saveCurso">
        <div slot="body">

            <h4 class="text-primary block m-b-lg">{{title}}</h4>

            <form v-bind:id="form" data-parsley-validate="">
                <template>

                    <div class="form-group">
                        <label>Nombre</label>
                        <input class="form-control" v-model="curso.nombre" required="true"/>
                    </div>

                </template>
            </form>
        </div>
    </modal-vik>
</template>

<script>

    module.exports = {

        data() {
            return {
                form: "id-form-curso-nivelacion",
                title: "",
                curso: {id: null, nombre: ''},
                raptor: null,
                modalCurso: VUE_MODAL.structFormAjax({
                    id: "id-modal-curso",
                    okbtn: "Guardar",
                    okclass: "btn-primary"
                })
            };
        },
        methods: {

            abrirModal(raptor) {
                var form = $("#" + this.form);
                form.parsley().destroy();

                this.curso = {id: '', nombre: ''};
                this.title = "Nuevo Curso Nivelación";
                this.$refs.modalCurso.open();
                this.raptor = raptor;

            },
            editar(item, raptor) {
                var form = $("#" + this.form);
                form.parsley().destroy();

                this.curso = JSON.parse(JSON.stringify(item));
                this.title = "Editar Curso Nivelación";
                this.raptor = raptor;
                this.$refs.modalCurso.open();
                this.$refs.modalCurso.okbtn = "Actualizar";
            },

            saveCurso() {
                var form = $("#" + this.form);
                if (!form.parsley().validate()) {
                    return;
                }

                myUtils.axios(VUE_AXIOS.structModalClose({
                    url: `/${rutaModulo}/save`,
                    modal: this.$refs.modalCurso,
                    raptor: this.raptor,
                    body: this.curso
                }));
            }

        }
    };
</script>