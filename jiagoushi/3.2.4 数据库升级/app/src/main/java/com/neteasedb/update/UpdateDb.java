package com.neteasedb.update;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class UpdateDb {

    private String sql_rename;
    private String sql_create;
    private String sql_insert;
    private String sql_delete;

    public UpdateDb(Element element){
        NodeList sqls = element.getElementsByTagName("sql_rename");
        this.sql_rename = sqls.item(0).getTextContent();

        sqls = element.getElementsByTagName("sql_create");
        this.sql_create = sqls.item(0).getTextContent();

        sqls = element.getElementsByTagName("sql_insert");
        this.sql_insert = sqls.item(0).getTextContent();

        sqls = element.getElementsByTagName("sql_delete");
        this.sql_delete = sqls.item(0).getTextContent();
    }

    public String getSql_rename() {
        return sql_rename;
    }

    public String getSql_create() {
        return sql_create;
    }

    public String getSql_insert() {
        return sql_insert;
    }

    public String getSql_delete() {
        return sql_delete;
    }

}
