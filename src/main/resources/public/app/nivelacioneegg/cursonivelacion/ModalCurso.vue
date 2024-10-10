<template>
    <modal-vik ref="modalCurso"
               v-bind="modalCurso"
               v-bind:okaction="saveCurso">
        <div slot="body">

            <h4 class="text-primary block m-b-lg">{{title}}</h4>

            <form v-bind:id="form">
                <template>

                    <div class="form-group">
                        <label>Nombre</label>
                        <span class="item-form-control item-form-gray text-primary" v-model="curso.nombre">
                        </span>
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
                curso: {nombre: ''},
                modalCurso: VUE_MODAL.structFormAjax({
                    id: "id-modal-curso",
                    okbtn: "Nuevo",
                    okclass: "btn-primary"
                })
            };
        },
        methods: {
            open() {
                var form = $("#" + this.form);
                form.parsley().destroy();

//                this.raptor = raptor;
//                this.configNueva = JSON.parse(JSON.stringify(config));
                this.title = "Nuevo Curso Nivelación";
                this.$refs.modalCurso.open();
            },
            saveCurso() {
                var form = $("#" + this.form);
                if (!form.parsley().validate()) {
                    return;
                }

                myUtils.axios(VUE_AXIOS.structModalClose({
                    url: `/${rutaModulo}/saveCurso`,
                    modal: this.$refs.modalCurso,
                    body: this.curso.nombre
                }));
            },

            getModal() {
//                return this.$refs.modalEditar;
            },

            // metodos genericos
            getListIds(list) {
                return list.map(item => item.id).join(',');
            },
//            getObjectId: myUtils.getObjectId,
//            getObjectName: myUtils.getObjectName,
//            commas: myUtils.commas
        }
    };
</script>