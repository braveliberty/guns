package com.stylefeng.guns.api.user.film;

import com.stylefeng.guns.api.user.film.vo.*;

import java.util.List;

public interface FilmAsyncServiceApi {

    //获取影片描述信息
    FilmDescVO getFilmDesc(String filmId);
    //获取图片信息
    ImgVO getImgs(String filmId);
    //获取演员信息
    ActorVO getDectInfo(String filmId);
    List<ActorVO> getActors(String filmId);

}
