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

            open(raptor) {

                var form = $("#" + this.form);
                form.parsley().destroy();

//                this.raptor = raptor;
//                this.configNueva = JSON.parse(JSON.stringify(config));
                this.title = "Nuevo Curso Nivelación";
                this.$refs.modalCurso.open();
                this.raptor = raptor;

            },
            saveCurso() {
                var form = $("#" + this.form);
                console.dir(form);
                if (!form.parsley().validate()) {
                    return;
                }

//
//                console.log("body")
//                console.dir(JSON.stringify(this.curso));
////                return;

                myUtils.axios(VUE_AXIOS.structModalClose({
                    url: `/${rutaModulo}/save`,
                    modal: this.$refs.modalCurso,
                    raptor: this.raptor,
                    body: this.curso
                }));


//                axios.post(
//                        `/${rutaModulo}/saveCurso`,
//                        {id: null, nombre: this.curso.nombre})
//                        .then(response => {
//                            console.log(response.data);  // Usuario creado
//                            this.$refs.modalCurso.close();
//                        })
//                        .catch(error => {
//                            console.error(error);
//                        });
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