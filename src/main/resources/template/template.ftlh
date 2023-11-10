<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${dao_path}">
    <sql id="allColumns">
        <#list filedMap?keys as key>
            ${key} AS ${filedMap[key]}<#if !last>,</#if>
        </#list>
        `id` AS id,
        `user_name` AS userName,
        `code`,
        `run_result` AS runResult,
        `is_deleted` AS deleted,
        `gmt_created` AS gmtCreated,
        `gmt_modified` AS gmtModified,
        `request_id` AS requestId
    </sql>

    <select id="query" resultType="com.yunfei.toolmaker.po.CodeDo">
        SELECT
        <include refid="userAllColumns"></include>
        FROM
        `${tableName}`
        <where>
            <#list filedArray as item>
                <if test="${item} != null">
                    AND `${tableName}`.`${item}` = ${r"#{"}${item}${r"}"}
                </if>
                AND
            </#list>

            `${tableName}`.`is_deleted` = 0
        </where>
    </select>

</mapper>