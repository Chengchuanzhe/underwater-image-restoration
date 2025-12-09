package com.underwater.mapper;

import com.underwater.entity.ImageRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ImageRecordMapper {
    // 新增图片记录
    @Insert("insert into image_restoration_record (user_id, original_filename, original_file_path, original_file_url, file_size, restoration_time) " +
            "values (#{userId}, #{originalFilename}, #{originalFilePath}, #{originalFileUrl}, #{fileSize}, now())")
    int insert(ImageRecord record);

    // 根据ID查询记录
    @Select("select * from image_restoration_record where id = #{id}")
    ImageRecord selectById(Long id);

    // 根据用户ID查询所有记录
    @Select("select * from image_restoration_record where user_id = #{userId} order by restoration_time desc")
    List<ImageRecord> selectByUserId(Long userId);

    // 更新修复图路径
    @Update("update image_restoration_record set restored_file_path = #{restoredFilePath}, restored_file_url = #{restoredFileUrl} where id = #{id}")
    int updateById(ImageRecord record);
}