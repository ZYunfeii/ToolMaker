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

    <insert id="insert" parameterType="${entityPath}">
        INSERT INTO `${tableName}` (<#list filedMap?keys as key>`${key}`<#if !key?is_last>,</#if></#list>)
        VALUES (<#list filedMap?keys as key><#if key == "gmt_created" || key == "gmt_modified">now()<#elseif key == "is_deleted">0<#else>${r"#{"}${filedMap[key]}${r"}"}</#if><#if !key?is_last>,</#if></#list>)
    </insert>

    <update id="update" parameterType="${entityPath}">
        UPDATE `${tableName}`
        <set>
            <#list filedMap?keys as key>
                <if test="${filedMap[key]} != null">
                    `${tableName}`.`${key}` = ${r"#{"}${filedMap[key]}${r"}"},
                </if>
            </#list>
            `gmt_modified` = now()
        </set>
        WHERE
        [[这里替换为条件]]
    </update>

    <update id="delete" parameterType="${entityPath}">
        UPDATE `${tableName}`
        SET is_deleted = 1
        WHERE
        <choose>
            <#list filedMap?keys as key>
            <when test="${filedMap[key]} != null">
                `${tableName}`.`${key}` = ${r"#{"}${filedMap[key]}${r"}"}
            </when>
            </#list>
        </choose>
        AND is_deleted = 0
    </update>

</mapper>