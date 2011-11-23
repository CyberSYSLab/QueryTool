/*
 * KeyWords.java
 *
 * Copyright (c) 2011 Vladimir V..
 *
 * This file is part of QueryTool.
 *
 * QueryTool is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * QueryTool is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * QueryTool. If not, see <http ://www.gnu.org/licenses/>.
 */
package org.querytool.utils;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author Vladimir V.
 */
public class KeyWords {
    private static final String SQL_WORDS = 
            "ADD ALL ALLOCATE ALTER AND ANY ARE ARRAY AS ASENSITIVE ASYMMETRIC AT ATOMIC AUTHORIZATION "+
            "BEGIN BETWEEN BIGINT BINARY BLOB BOOLEAN BOTH BY CALL CALLED CASCADED CASE CAST "+
            "CHAR CHARACTER CHECK CLOB CLOSE COLLATE COLUMN COMMIT CONDITION CONNECT CONSTRAINT"+
            "CONTINUE CORRESPONDING CREATE CROSS CUBE CURRENT CURRENT_DATE CURRENT_DEFAULT_TRANSFORM_GROUP "+
            "CURRENT_PATH CURRENT_ROLE CURRENT_TIME CURRENT_TIMESTAMP CURRENT_TRANSFORM_GROUP_FOR_TYPE "+
            "CURRENT_USER CURSOR CYCLE DATE DAY DEALLOCATE DEC DECIMAL DECLARE DEFAULT DELETE "+
            "DEREF DESCRIBE DETERMINISTIC DISCONNECT DISTINCT DO DOUBLE DROP DYNAMIC EACH "+
            "ELEMENT ELSE ELSEIF END ESCAPE EXCEPT EXEC EXECUTE EXISTS EXIT EXTERNAL FALSE FETCH FILTER "+
            "FLOAT FOR FOREIGN FREE FROM FULL FUNCTION GET GLOBAL GRANT GROUP GROUPING HANDLER "+
            "HAVING HOLD HOUR IDENTITY IF IMMEDIATE IN INDICATOR INNER INOUT INPUT INSENSITIVE "+
            "INSERT INT INTEGER INTERSECT INTERVAL INTO IS ITERATE JOIN LANGUAGE LARGE LATERAL "+
            "LEADING LEAVE LEFT LIKE LOCAL LOCALTIME LOCALTIMESTAMP LOOP MATCH MEMBER MERGE METHOD "+
            "MINUTE MODIFIES MODULE MONTH MULTISET NATIONAL NATURAL NCHAR NCLOB NEW NO NONE NOT "+
            "NULL NUMERIC OF OLD ON ONLY OPEN OR ORDER OUT OUTER OUTPUT OVER OVERLAPS PARAMETER "+
            "PARTITION PRECISION PREPARE PRIMARY PROCEDURE RANGE READS REAL RECURSIVE REF REFERENCES "+
            "REFERENCING RELEASE REPEAT RESIGNAL RESULT RETURN RETURNS REVOKE RIGHT ROLLBACK "+
            "ROLLUP ROW ROWS SAVEPOINT SCOPE SCROLL SEARCH SECOND SELECT SENSITIVE SESSION_USER "+
            "SET SIGNAL SIMILAR SMALLINT SOME SPECIFIC SPECIFICTYPE SQL SQLEXCEPTION SQLSTATE "+
            "SQLWARNING START STATIC SUBMULTISET SYMMETRIC SYSTEM SYSTEM_USER TABLE TABLESAMPLE "+
            "THEN TIME TIMESTAMP TIMEZONE_HOUR TIMEZONE_MINUTE TO TRAILING TRANSLATION TREAT "+
            "TRIGGER TRUE UNDO UNION UNIQUE UNKNOWN UNNEST UNTIL UPDATE USER USING VALUE VALUES "+
            "VARCHAR VARYING WHEN WHENEVER WHERE WHILE WINDOW WITH WITHIN WITHOUT YEAR";
    
    public static ArrayList getKeyWords() {
        final StringTokenizer words = new StringTokenizer(SQL_WORDS, " ");
        ArrayList wordsList = new ArrayList();

        while (words.hasMoreTokens()) {
            String word = words.nextToken();
            wordsList.add(word);
        }

        return wordsList;
    }
}
