package com.TwoSeaU.BaData.domain.store.service;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.WKTReader;

public class GeoUtils {

    private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);


    public static Polygon createBoundingBoxByWKT(double swLat, double swLng, double neLat, double neLng) {

        String wkt = String.format(
                "POLYGON((%f %f, %f %f, %f %f, %f %f, %f %f))",
                swLng, swLat, // 좌측 하단
                neLng, swLat, // 우측 하단
                neLng, neLat, // 우측 상단
                swLng, neLat, // 좌측 상단
                swLng, swLat  // 다시 좌측 하단 (폴리곤 닫기)
        );

        try {
            WKTReader wktReader = new WKTReader(geometryFactory);
            Polygon boundingBox = (Polygon) wktReader.read(wkt);
            boundingBox.setSRID(4326);
            return boundingBox;
        } catch (Exception e) {
            // 로깅 추가 가능
            return null;
        }
    }

    public static Polygon createBoundingBoxByCoordinate(double swLat, double swLng, double neLat, double neLng) {

        Coordinate[] coords = new Coordinate[]{
                new Coordinate(swLng, swLat), // 좌하단
                new Coordinate(neLng, swLat), // 우하단
                new Coordinate(neLng, neLat), // 우상단
                new Coordinate(swLng, neLat), // 좌상단
                new Coordinate(swLng, swLat)  // 닫기
        };

        Polygon polygon = geometryFactory.createPolygon(coords);
        polygon.setSRID(4326);
        return polygon;
    }

    public static Point makeByCoordinate(double longtitude,double latitude){

        return geometryFactory.createPoint(new Coordinate(longtitude, latitude));
    }


}
