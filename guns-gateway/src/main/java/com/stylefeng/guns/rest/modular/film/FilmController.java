package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcContext;
import com.stylefeng.guns.api.user.film.FilmAsyncServiceApi;
import com.stylefeng.guns.api.user.film.FilmServiceApi;
import com.stylefeng.guns.api.user.film.vo.*;
import com.stylefeng.guns.rest.modular.film.vo.FilmConditionVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmIndexVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmRequestVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@RequestMapping(("/film"))
public class FilmController {

    @Reference(interfaceClass = FilmServiceApi.class)
    private FilmServiceApi filmServiceApi;

    @Reference(interfaceClass = FilmAsyncServiceApi.class,async = true)
    private FilmAsyncServiceApi filmAsyncServiceApi;
    /**
     * 服务聚合的好处和坏处
     * 好处：
     *  1. 六个接口一次请求，省下了五次http请求
     *  2. 同一接口对外暴露，降低了前后端分离开发的难度
     *
     * 坏处：
     *  1. 一次获取数据过多容易出现问题
     *
     * @return
     */
    @GetMapping(value = "getIndex")
     public ResponseVO getIndex() {
        FilmIndexVO filmIndexVO = new FilmIndexVO();
        // 1. 获取Banner图
        filmIndexVO.setBanners(filmServiceApi.getBanners());
        // 2. 获取热映影片
        filmIndexVO.setHotFilms(filmServiceApi.getHotFilms(true,8,1,1,99,99,99));
        // 3. 即将上映的电影
        filmIndexVO.setSoonFilms(filmServiceApi.getSoonFilms(true,8,1,1,99,99,99));
        // 4. 票房排行
        filmIndexVO.setBoxRanking(filmServiceApi.getBoxRanking());
        // 5. 获取受欢迎榜单
        filmIndexVO.setExpectRanking(filmServiceApi.getExpectRanking());
        // 6. 获取top100数据
        filmIndexVO.setTop100(filmServiceApi.getTop());

        return ResponseVO.success(filmIndexVO);
     }

     @RequestMapping(value = "getConditionList",method = RequestMethod.GET)
     public ResponseVO getConditionList(@RequestParam(name = "catId",required = false,defaultValue = "99") String catId,
                                        @RequestParam(name = "sourceId",required = false,defaultValue = "99") String sourceId,
                                        @RequestParam(name = "yearId",required = false,defaultValue = "99") String yearId){

         // 类型集合
         // 标识位
         boolean flag = false;
         // 类型集合
         List<CatVO> cats = filmServiceApi.getCats();
         List<CatVO> catResult = new ArrayList<>();
         CatVO cat = null;

         for (CatVO catVO:cats) {
             // 判断集合是否存在catId，如果存在，则将对应的实体变为active状态
             if (catVO.getCatId().equals("99")) {
                 cat = catVO;
                 continue;
             }
             if (catVO.getCatId().equals(catId)) {
                 flag = true;
                 catVO.setActive(true);
             } else {
                 catVO.setActive(false);
             }
             catResult.add(catVO);

         }
         if (!flag) {
             cat.setActive(true);
             catResult.add(cat);
         } else {
             catResult.add(cat);
         }
         // 片源集合
         flag = false;
         List<SourceVO> sources = filmServiceApi.getSources();
         List<SourceVO> sourceResult = new ArrayList<>();
         SourceVO sourceVO = null;

         for (SourceVO source:sources) {
             // 判断集合是否存在catId，如果存在，则将对应的实体变为active状态
             if (source.getSourceId().equals("99")) {
                 sourceVO = source;
                 continue;
             }
             if (source.getSourceId().equals(catId)) {
                 flag = true;
                 source.setActive(true);
             } else {
                 source.setActive(false);
             }
             sourceResult.add(source);

         }
         if (!flag) {
             sourceVO.setActive(true);
             sourceResult.add(sourceVO);
         } else {
             sourceResult.add(sourceVO);
         }

         // 年代集合

         flag = false;
         List<YearVO> years = filmServiceApi.getYears();
         List<YearVO> yearResult = new ArrayList<>();
         YearVO yearVO = null;

         for (YearVO year:years) {
             // 判断集合是否存在catId，如果存在，则将对应的实体变为active状态
             if (year.getYearId().equals("99")) {
                 yearVO = year;
                 continue;
             }
             if (year.getYearId().equals(catId)) {
                 flag = true;
                 year.setActive(true);
             } else {
                 year.setActive(false);
             }
             yearResult.add(year);

         }
         if (!flag) {
             yearVO.setActive(true);
             yearResult.add(yearVO);
         } else {
             yearResult.add(yearVO);
         }
         FilmConditionVO filmConditionVO = new FilmConditionVO();

         filmConditionVO.setCatInfo(catResult);
         filmConditionVO.setSourceInfo(sourceResult);
         filmConditionVO.setYearInfo(yearResult);

         return ResponseVO.success(filmConditionVO);

     }

     @RequestMapping(value = "/getFilms",method = RequestMethod.GET)
     public ResponseVO getFilms(FilmRequestVO filmRequestVO) {

        String img_pre = "http://img.meetingshop.cn/cn";
         // 根据showType 判断影片查询类型
         FilmVO filmVO = null;
         switch (filmRequestVO.getShowType()) {
             case 1: filmVO = filmServiceApi.getHotFilms(false,filmRequestVO.getPageSize(),filmRequestVO.getNowPage(),
                     filmRequestVO.getSortId(),filmRequestVO.getSourceId(),
                     filmRequestVO.getYearId(), filmRequestVO.getCatId());
                    break;
             case 2: filmVO = filmServiceApi.getSoonFilms(false,filmRequestVO.getPageSize(),filmRequestVO.getNowPage(),
                     filmRequestVO.getSortId(),filmRequestVO.getSourceId(),
                     filmRequestVO.getYearId(), filmRequestVO.getCatId());
                 break;
             case 3:filmVO = filmServiceApi.getClassicFilms(filmRequestVO.getPageSize(),filmRequestVO.getNowPage(),
                     filmRequestVO.getSortId(),filmRequestVO.getSourceId(),
                     filmRequestVO.getYearId(), filmRequestVO.getCatId());
                 break;
             default: filmVO = filmServiceApi.getHotFilms(false,filmRequestVO.getPageSize(),filmRequestVO.getNowPage(),
                     filmRequestVO.getSortId(),filmRequestVO.getSourceId(),
                     filmRequestVO.getYearId(), filmRequestVO.getCatId());
                 break;
         }
         // 根据sortId 排序
         // 添加各种查询条件
         // 判断当前是第几页


        return ResponseVO.success(filmVO.getNowPage(),filmVO.getTotalPage(),img_pre,filmVO.getFilmInfos());
     }

     @RequestMapping(value = "films/{searchParam}", method = RequestMethod.GET)
     public ResponseVO films(@PathVariable("searchParam") String searchParam, int searchType) throws ExecutionException, InterruptedException {
        //根据searchType判断查询类型
            FilmDetailVO filmDetail = filmServiceApi.getFilmDetail(searchType,searchParam);
         //不同查询类型，判断条件略有不同
         //查询影片的详细信息-->dubbo的异步获取

         if(filmDetail.getFilmId() ==null){
             return ResponseVO.serviceFail("没有可查询影片");
         }else if (filmDetail.getFilmId() == null ||filmDetail.getFilmId().trim().length()==0){
             return ResponseVO.serviceFail("没有可查询影片");
         }
            String filmId = filmDetail.getFilmId();

         //获取影片描述信息，换成异步调用
//         FilmDescVO filmDescVO = filmAsyncServiceApi.getFilmDesc(filmId);
         filmAsyncServiceApi.getFilmDesc(filmId);
         Future<FilmDescVO> filmDescVOFuture = RpcContext.getContext().getFuture();
         //获取图片信息
//         ImgVO imgVO = filmAsyncServiceApi.getImgs(filmId);
         filmAsyncServiceApi.getImgs(filmId);
        Future<ImgVO> imgVOFuture = RpcContext.getContext().getFuture();
         //获取导演信息
//         ActorVO direcotr = filmAsyncServiceApi.getDectInfo(filmId);
         filmAsyncServiceApi.getDectInfo(filmId);
         Future<ActorVO> direcotrVOFuture = RpcContext.getContext().getFuture();
         //获取演员信息
//         List<ActorVO> actors = filmAsyncServiceApi.getActors(filmId);
         filmAsyncServiceApi.getActors(filmId);
         Future<List<ActorVO>> actorVOListFuture = RpcContext.getContext().getFuture();

         InfoRequestVO infoRequestVO =new InfoRequestVO();
//         组织导演信息与演员信息
         ActorRequestVO actorRequestVO = new ActorRequestVO();
         actorRequestVO.setActors(actorVOListFuture.get());
         actorRequestVO.setDirector(direcotrVOFuture.get());
//         组织info对象
         infoRequestVO.setActors(actorRequestVO);
         infoRequestVO.setBiography(filmDescVOFuture.get().getBiography());
         infoRequestVO.setFilmId(filmId);
         infoRequestVO.setImgVO(imgVOFuture.get());

//         组合返回值
         filmDetail.setInfo04(infoRequestVO);
         return ResponseVO.success("img.meetingshop.cn",filmDetail);

    }

//     @RequestMapping(value = "films/{searchParam}",method = RequestMethod.GET)
//     public ResponseVO films(@PathVariable("searchParam") String searchParam,
//                             int searchType) {
//         // 根据searchType，判断查询类型
//         FilmDetailVO filmDetail = filmServiceApi.getFilmDetail(searchType, searchParam);
//         // 不同的查询类型，传入的条件会略有不同【】
//
//         // 查询影片的详细信息 -> Dubbo的异步获取
//        return null;
//     }
}
