package com.stylefeng.guns.rest.modular.film.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.user.film.FilmServiceApi;
import com.stylefeng.guns.api.user.film.vo.*;
import com.stylefeng.guns.core.util.DateUtil;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Service(interfaceClass = FilmServiceApi.class)
public class DefaultFilmServiceImpl implements FilmServiceApi {

    @Autowired
    private MoocBannerTMapper bannerTMapper;
    @Autowired
    private MoocFilmTMapper filmTMapper;
    @Autowired
    private MoocCatDictTMapper moocCatDictTMapper;
    @Autowired
    private MoocYearDictTMapper moocYearDictTMapper;
    @Autowired
    private MoocSourceDictTMapper moocSourceDictTMapper;
    @Autowired
    private MoocFilmTMapper moocFilmTMapper;
    @Autowired
    private MoocFilmInfoTMapper moocFilmInfoTMapper;
    @Autowired
    private MoocActorTMapper moocActorTMapper;
    @Override
    public List<BannerVO> getBanners() {
        List<BannerVO> list = new ArrayList<>();
        List<MoocBannerT> Banners = bannerTMapper.selectList(null);

        for (MoocBannerT bannerT : Banners) {

            BannerVO bannerVO = new BannerVO();
            bannerVO.setBannerId(bannerT.getUuid()+"");
            bannerVO.setBannerUrl(bannerT.getBannerUrl());
            bannerVO.setBannerAddress(bannerT.getBannerAddress());

            list.add(bannerVO);
        }
        return list;
    }

    private List<FilmInfo> getFilmInfos(List<MoocFilmT> moocFilmTS) {

        List<FilmInfo> filmInfos = new ArrayList<>();

        for (MoocFilmT moocFilmT: moocFilmTS) {
            FilmInfo filmInfo = new FilmInfo();
            filmInfo.setBoxNum(moocFilmT.getFilmBoxOffice());
            filmInfo.setExpectNum(moocFilmT.getFilmPresalenum());
            filmInfo.setFilmId(moocFilmT.getUuid()+"");
            filmInfo.setFilmName(moocFilmT.getFilmName());
            filmInfo.setFilmScore(moocFilmT.getFilmScore());
            filmInfo.setScore(moocFilmT.getFilmScore());
            filmInfo.setFilmType(moocFilmT.getFilmType());
            filmInfo.setImgAddress(moocFilmT.getImgAddress());
            filmInfo.setShowTime(DateUtil.getDay(moocFilmT.getFilmTime()));

            // 将转换的对象放入结果集
            filmInfos.add(filmInfo);
        }
        return filmInfos;
    }

    @Override
    public FilmVO getHotFilms(boolean isLimit, int nums,int nowPage,int sortId,int sourceId,int yearId,int catId) {

        FilmVO filmVO = new FilmVO();
        List<FilmInfo> filmInfos = new ArrayList<>();

        // 热映影片的限制条件
        EntityWrapper<MoocFilmT> wrapper = new EntityWrapper<>();
        wrapper.eq("film_status","1");
        // 判断是否是首页需要的内容
        if (isLimit) {

            Page<MoocFilmT> page = new Page<>(1,nums);
            List<MoocFilmT> moocFilmTS = filmTMapper.selectPage(page, wrapper);

            filmInfos = getFilmInfos(moocFilmTS);
            filmVO.setFilmNum(filmInfos.size());
            filmVO.setFilmInfos(filmInfos);
        }else {

            Page<MoocFilmT> page = null;

            switch (sortId) {

                case 1:
                    page = new Page<>(nowPage,nums,"film_box_office");
                    break;
                case 2:
                    page = new Page<>(nowPage,nums,"film_time");
                    break;
                case 3:
                    page = new Page<>(nowPage,nums,"film_score");
                    break;
                default:
                    page = new Page<>(nowPage,nums,"film_box_office");
                    break;
            }
            // 如果sourceId,yearId,catId不为99，则表示按照对应的编号进行查询
            if (sourceId != 99) {
                wrapper.eq("film_source",sortId);
            }
            if (yearId != 99) {
                wrapper.eq("film_date",yearId);
            }
            if (catId != 99) {
                String catStr = "%#"+catId+"#%";
                wrapper.like("film_cats",catStr);
            }
            List<MoocFilmT> moocFilmTS = filmTMapper.selectPage(page, wrapper);

            filmInfos = getFilmInfos(moocFilmTS);
            filmVO.setFilmNum(filmInfos.size());

            // 需要总页数totalCounts/nums -> 0 + 1 = 1

            int totalCounts = filmTMapper.selectCount(wrapper);
            int totalPages = totalCounts / nums + 1;

            filmVO.setFilmInfos(filmInfos);
            filmVO.setTotalPage(totalPages);
            filmVO.setNowPage(nowPage);
        }
        return filmVO;
    }



    @Override
    public FilmVO getSoonFilms(boolean isLimit, int nums,int nowPage,int sortId,int sourceId,int yearId,int catId) {

        FilmVO filmVO = new FilmVO();
        List<FilmInfo> filmInfos = new ArrayList<>();

        // 热映影片的限制条件
        EntityWrapper<MoocFilmT> wrapper = new EntityWrapper<>();
        wrapper.eq("film_status","2");
        // 判断是否是首页需要的内容
        if (isLimit) {

            Page<MoocFilmT> page = new Page<>(1,nums);
            List<MoocFilmT> moocFilmTS = filmTMapper.selectPage(page, wrapper);

            filmInfos = getFilmInfos(moocFilmTS);
            filmVO.setFilmNum(filmInfos.size());
            filmVO.setFilmInfos(filmInfos);
        }else {
            // 即将上映影片
            Page<MoocFilmT> page = null;

            switch (sortId) {

                case 1:
                    page = new Page<>(nowPage,nums,"film_preSaleNum");
                    break;
                case 2:
                    page = new Page<>(nowPage,nums,"film_time");
                    break;
                case 3:
                    page = new Page<>(nowPage,nums,"film_preSaleNum");
                    break;
                default:
                    page = new Page<>(nowPage,nums,"film_preSaleNum");
                    break;
            }
            // 如果sourceId,yearId,catId不为99，则表示按照对应的编号进行查询
            if (sourceId != 99) {
                wrapper.eq("film_source",sortId);
            }
            if (yearId != 99) {
                wrapper.eq("film_date",yearId);
            }
            if (catId != 99) {
                String catStr = "%#"+catId+"#%";
                wrapper.like("film_cats",catStr);
            }
            List<MoocFilmT> moocFilmTS = filmTMapper.selectPage(page, wrapper);

            filmInfos = getFilmInfos(moocFilmTS);
            filmVO.setFilmNum(filmInfos.size());

            // 需要总页数totalCounts/nums -> 0 + 1 = 1

            int totalCounts = filmTMapper.selectCount(wrapper);
            int totalPages = totalCounts / nums + 1;

            filmVO.setFilmInfos(filmInfos);
            filmVO.setTotalPage(totalPages);
            filmVO.setNowPage(nowPage);
        }
        return filmVO;
    }

    @Override
    public FilmVO getClassicFilms(int nums, int nowPage, int sortId, int sourceId, int yearId, int catId) {
        FilmVO filmVO = new FilmVO();
        List<FilmInfo> filmInfos = new ArrayList<>();

        // 热映影片的限制条件
        EntityWrapper<MoocFilmT> wrapper = new EntityWrapper<>();
        wrapper.eq("film_status","3");

        Page<MoocFilmT> page = null;

        switch (sortId) {

            case 1:
                page = new Page<>(nowPage,nums,"film_box_office");
                break;
            case 2:
                page = new Page<>(nowPage,nums,"film_time");
                break;
            case 3:
                page = new Page<>(nowPage,nums,"film_score");
                break;
            default:
                page = new Page<>(nowPage,nums,"film_box_office");
                break;
        }
        // 如果sourceId,yearId,catId不为99，则表示按照对应的编号进行查询
        if (sourceId != 99) {
            wrapper.eq("film_source",sortId);
        }
        if (yearId != 99) {
            wrapper.eq("film_date",yearId);
        }
        if (catId != 99) {
            String catStr = "%#"+catId+"#%";
            wrapper.like("film_cats",catStr);
        }
        List<MoocFilmT> moocFilmTS = filmTMapper.selectPage(page, wrapper);

        filmInfos = getFilmInfos(moocFilmTS);
        filmVO.setFilmNum(filmInfos.size());

        // 需要总页数totalCounts/nums -> 0 + 1 = 1

        int totalCounts = filmTMapper.selectCount(wrapper);
        int totalPages = totalCounts / nums + 1;

        filmVO.setFilmInfos(filmInfos);
        filmVO.setTotalPage(totalPages);
        filmVO.setNowPage(nowPage);
        return null;
    }

    @Override
    public List<FilmInfo> getBoxRanking() {
        // 条件 -> 正在上映的，票房前10名
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status","1");

        Page<MoocFilmT> page = new Page<>(1,10,"film_box_office");

        List<MoocFilmT> moocFilms = filmTMapper.selectPage(page,entityWrapper);
        List<FilmInfo> filmInfos = getFilmInfos(moocFilms);
        return filmInfos;
    }

    @Override
    public List<FilmInfo> getExpectRanking() {
        // 条件 -> 正在上映的，票房前10名
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status","1");

        Page<MoocFilmT> page = new Page<>(1,10,"film_preSaleNum");

        List<MoocFilmT> moocFilms = filmTMapper.selectPage(page,entityWrapper);
        List<FilmInfo> filmInfos = getFilmInfos(moocFilms);
        return filmInfos;
    }

    @Override
    public List<FilmInfo> getTop() {
        // 条件 -> 正在上映的，票房前10名
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status","1");

        Page<MoocFilmT> page = new Page<>(1,10,"film_score");

        List<MoocFilmT> moocFilms = filmTMapper.selectPage(page,entityWrapper);
        List<FilmInfo> filmInfos = getFilmInfos(moocFilms);
        return filmInfos;
    }

    @Override
    public List<CatVO> getCats() {
        List<CatVO> cats = new ArrayList<>();
        // 查询实体对象 - MoocCatDicT
        List<MoocCatDictT> moocCats = moocCatDictTMapper.selectList(null);
        // 将实体对象转换为业务对象 - CatVO
        for (MoocCatDictT moocCatDictT:moocCats) {
            CatVO catVO = new CatVO();

            catVO.setCatId(moocCatDictT.getUuid()+"");
            catVO.setCatName(moocCatDictT.getShowName());

            cats.add(catVO);
        }

        return cats;
    }

    @Override
    public List<SourceVO> getSources() {

        List<SourceVO> sources = new ArrayList<>();

        List<MoocSourceDictT> moocSourceDictTS = moocSourceDictTMapper.selectList(null);
        for (MoocSourceDictT moocSourceDictT : moocSourceDictTS) {
            SourceVO sourceVO = new SourceVO();

            sourceVO.setSourceId(moocSourceDictT.getUuid()+"");
            sourceVO.setSourceName(moocSourceDictT.getShowName());

            sources.add(sourceVO);
        }
        return sources;
    }

    @Override
    public List<YearVO> getYears() {
        List<YearVO> years = new ArrayList<>();
        // 查询实体对象 - MoocYearDictT
        List<MoocYearDictT> moocYears = moocYearDictTMapper.selectList(null);

        // 将实体对象转换为业务对象 - YearVO
        for (MoocYearDictT moocYearDictT: moocYears) {
            YearVO yearVO = new YearVO();

            yearVO.setYearId(moocYearDictT.getUuid()+"");
            yearVO.setYearName(moocYearDictT.getShowName());

            years.add(yearVO);
        }

        return years;
    }
//获取影片详情接口，前一半信息
    @Override
    public FilmDetailVO getFilmDetail(int searchType,String searchParam) {

        FilmDetailVO filmDetailVO = null;
        //1 按名称查找， 2 按id查找
        if (searchType == 1) {
            filmDetailVO = moocFilmTMapper.getFilmDetailByName(searchParam);
        } else {
            filmDetailVO = moocFilmTMapper.getFilmDetailById(searchParam);
        }

        return filmDetailVO;
    }

    private MoocFilmInfoT getFilmInfo(String filmId){
        MoocFilmInfoT moocFilmInfoT = new MoocFilmInfoT();
        moocFilmInfoT.setFilmId(filmId);

        moocFilmInfoT = moocFilmInfoTMapper.selectOne(moocFilmInfoT);
        return  moocFilmInfoT;
    }
//    获取影片详情接口，后一半信息
    @Override
    public FilmDescVO getFilmDesc(String filmId) {
        MoocFilmInfoT moocFilmInfoT = getFilmInfo(filmId);

        FilmDescVO filmDescVO = new FilmDescVO();
        filmDescVO.setBiography(moocFilmInfoT.getBiography());
        filmDescVO.setFilmId(filmId);
        return filmDescVO;
    }

    @Override
    public ImgVO getImgs(String filmId) {
        MoocFilmInfoT moocFilmInfoT = getFilmInfo(filmId);
        String filmImgStr = moocFilmInfoT.getFilmImgs();
        String [] filmImgs = filmImgStr.split(",");

        ImgVO imgVO = new ImgVO();
        imgVO.setMainImg(filmImgs[0]);
        imgVO.setImg01(filmImgs[1]);
        imgVO.setImg01(filmImgs[2]);
        imgVO.setImg01(filmImgs[3]);
        imgVO.setImg01(filmImgs[4]);

        return imgVO;
    }

    @Override
    public ActorVO getDectInfo(String filmId) {
        MoocFilmInfoT moocFilmInfoT =getFilmInfo(filmId);
        Integer directorId = moocFilmInfoT.getDirectorId();

        MoocActorT moocActorT = moocActorTMapper.selectById(directorId);

        ActorVO actorVO = new ActorVO();
        actorVO.setDirectorName(moocActorT.getActorName());
        actorVO.setImgAddress(moocActorT.getActorImg());
        return actorVO;
    }

    @Override
    public List<ActorVO> getActors(String filmId) {
        List<ActorVO> actorVOList = moocActorTMapper.getActors(filmId);

        return actorVOList;
    }
    //获取影片描述信息
    //获取图片信息
    //获取演员信息

}
