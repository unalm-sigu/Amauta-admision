let $dynatable = null;
$('#dynaTable').dynatable({});

Vue.component("dynatable", {
    template: "#dynatableTemplate",
    props: ["project", "dynatable"],
    mounted: function () {
        let $vue = this;
        $vue.oficina = JSON.parse(oficinaId);
        $vue.createDynatable();

        $global.$on("reloadDyntable", function () {
            $dynatable.process();
        });
        $('.dynatable-search').addClass('col-md-2 pull-right');
        $('.dynatable-search').find('input')
                .addClass('form-control input-sm')
                .attr('placeholder', 'Buscar');
    },
    methods: {
        createDynatable: function () {
            let $vue = this;

            $dynatable = $('#dynaTable').dynatable({
                dataset: {
                    ajaxUrl: APP.url('general/oficina/list/' + $vue.oficina),
                    perPageDefault: 100
                },
                writers: {_rowWriter: $vue.writter},
                table: {bodyRowSelector: "tbody tr"}
            }).data('dynatable');

            $("body").delegate(".cancelarCita", "click", function () {
                $global.$emit("cancelarCita", $(this).attr("rel"));
            });
            var divElegido = null;
            $("body").delegate(".verModalidades", "click", function (e) {
                let $this = $(this);
                var div = $this.closest("div");
                var classColor = 'bg-light';
                var tieneBgColor = div.hasClass(classColor);
                $dynatable.queries.remove("co.estado");
                if (divElegido !== null) {
                    divElegido.removeClass(classColor);
                    divElegido = null;
                }

                if (!tieneBgColor) {
                    div.addClass(classColor);
                    var estado = $this.attr("rel");
                    $dynatable.queries.add("co.estado", estado);
                    divElegido = div;
                }
                $dynatable.process();
            });
        },
        writter: function (rowIndex, record, columns, cellWriter) {
            record.index = rowIndex;
            var html = $.templates("#dynatableRowTemplate").render(record);
            return $(html).prop('outerHTML');
        }
    }
});

new Vue({
    el: '#colaboradorVue',
    data: {
    },
    computed: {

    },
    created() {
        let $vue = this;

    },
    mounted: function () {
    },
    methods: {

    }
});
