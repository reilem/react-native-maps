//
//  AIRGoogleMapLocalTile.h
//  react-native-google-maps
//
//  Created by Reinert Lemmens on 06/08/2018.
//

#import <Foundation/Foundation.h>
#import <GoogleMaps/GoogleMaps.h>
#import <AIRGoogleMapLocalTileOverlay.h>
#import <React/RCTViewManager.h>

@interface AIRGoogleMapLocalTile : UIView

@property (nonatomic, strong) AIRGoogleMapLocalTileOverlay *tileLayer;

@property (nonatomic, strong) NSString *fileTemplate;
@property (nonatomic, strong) NSString *urlTemplate;
@property (nonatomic, weak) NSArray *tempRange;
@property (nonatomic, strong) NSArray *currentTempRange;

@end
