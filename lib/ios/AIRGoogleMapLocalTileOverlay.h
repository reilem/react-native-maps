//
//  AIRGoogleMapRealLocalTile.h
//  react-native-google-maps
//
//  Created by Reinert Lemmens on 06/08/2018.
//

#import <Foundation/Foundation.h>
#import <GoogleMaps/GoogleMaps.h>
#import "PresetColor.h"
#import "SimpleColor.h"


@interface AIRGoogleMapLocalTileOverlay : GMSTileLayer

@property (nonatomic, strong) NSString *fileTemplate;
@property (nonatomic, strong) NSString *urlTemplate;
@property (nonatomic, strong) NSArray *maxTempRange;
@property (nonatomic, strong) NSArray *currentTempRange;

- (NSURL *)urlInternetPathForZ:(NSUInteger)z x:(NSUInteger)x y:(NSUInteger)y;
- (NSURL *)urlFilePathForZ:(NSUInteger)z x:(NSUInteger)x y:(NSUInteger)y;

- (NSString *)parsePath:(NSString *)path z:(NSUInteger)z x:(NSUInteger)x y:(NSUInteger)y;
- (UIImage *)processImage:(UIImage *)image;

@end
