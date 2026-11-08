package org.delcom.ifs25007pbo2026springbootcashflow.framework;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.communication.IndexHtmlResponse;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

/**
 * Melengkapi halaman awal (index.html) dengan atribut bahasa, judul, dan meta description
 * agar memenuhi pemeriksaan SEO dan aksesibilitas.
 */
@Component
public class PageMetadata implements VaadinServiceInitListener {
    private static final String TITLE = "Cashflow";
    private static final String DESCRIPTION =
            "Aplikasi web pencatat pemasukan dan pengeluaran untuk memantau saldo keuangan pribadi.";

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.addIndexHtmlRequestListener(this::decorate);
    }

    private void decorate(IndexHtmlResponse response) {
        Document document = response.getDocument();

        Element html = document.selectFirst("html");
        if (html != null) {
            html.attr("lang", "id");
        }

        Element head = document.head();
        if (head.select("title").isEmpty()) {
            head.appendElement("title").text(TITLE);
        }
        if (head.select("meta[name=description]").isEmpty()) {
            head.appendElement("meta").attr("name", "description").attr("content", DESCRIPTION);
        }
    }
}
