<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${daoPath}">
    <sql id="allColumns">
        <#list filedMap?keys as key>
        ${key} AS ${filedMap[key]}<#if !key?is_last>,</#if>
        </#list>
    </sql>

    <select id="query" resultType="${entityPath}">
        SELECT
        <include refid="allColumns"></include>
        FROM
        `${tableName}`
        <where>
            <#list filedMap?keys as key>
            <if test="${filedMap[key]} != null">
                AND `${tableName}`.`${key}` = ${r"#{"}${filedMap[key]}${r"}"}
            </if>
            </#list>
            AND
            `${tableName}`.`is_deleted` = 0
        </where>
    </select>

</mapper>