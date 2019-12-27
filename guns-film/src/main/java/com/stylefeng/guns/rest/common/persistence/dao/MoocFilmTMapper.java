package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.api.user.film.vo.FilmDetailVO;
import com.stylefeng.guns.rest.common.persistence.model.MoocFilmT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


/**
 * <p>
 * 影片主表 Mapper 接口
 * </p>
 *
 * @author 徐思思
 * @since 2019-12-05
 */
@Repository
@Mapper
public interface MoocFilmTMapper extends BaseMapper<MoocFilmT> {

    FilmDetailVO getFilmDetailByName(@Param("filmName") String filmName);

    FilmDetailVO getFilmDetailById(@Param("uuid") String uuid);
}
