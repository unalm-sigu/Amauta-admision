$(function () {
    var lastIdGrupo = null; // <- Controla el grupo anterior

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/programacion/resumen/listGrupo'),
            perPageDefault: 10,
            ajaxData: {departamento: $('#txtDepartamentoAcademico').val()}
        },
        writers: {
            _rowWriter: ulWriter
        },
        table: {
            bodyRowSelector: 'tbody tr'
        }
    }).data('dynatable');

    function ulWriter(rowIndex, record, columns, cellWriter) {
        var colorEstadoPlan = {
            ACT: "success", APR: "success", EXPR: "success", EXP: "success", ACEP: "success",
            PEND: "warning", CRE: "warning", OBS: "warning", SOL: "warning", REE: "warning", PRO: "warning",
            INA: "danger", RHZ: "danger", CER: "danger"
        };
        var colorEstadoActa = {ABI: "danger", CER: "success", RAB: "danger"};
        var estado = {ACT: "success", INA: "danger", CER: "danger"};

        record.colorEstadoGrupo = colorEstadoActa[record.estadoGrupo];
        record.colorEstadoPlan = colorEstadoPlan[record.estadoPlan];
        record.colorEstado = estado[record.estado];
        record.index = rowIndex;

        var secciones = record.secciones ? record.secciones.split(",") : [];
        var grupoHoras = record.grupoHoras ? record.grupoHoras.split(",") : [];

        var html = "";

        // Encabezado de grupo
        if (record.idGrupo !== lastIdGrupo) {
            lastIdGrupo = record.idGrupo;
            html += `<tr class="info">
                    <td colspan="8"><strong>Grupo ID: ${record.idGrupo}</strong></td>
                 </tr>`;
        }

        for (var i = 0; i < secciones.length; i++) {
            var seccionNombre = secciones[i].split("|")[1] || '';
            var grupoHora = grupoHoras[i] ? grupoHoras[i].split("|")[1] : '';
            record.seccionActual = seccionNombre + (grupoHora ? ' - ' + grupoHora : '');

            html += $.templates("#templateGrupos").render(record);
        }

        return html;
    }

});
