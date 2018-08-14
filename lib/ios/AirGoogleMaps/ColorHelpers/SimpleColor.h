//
//  SimpleColor.h
//  react-native-google-maps
//
//  Created by Reinert Lemmens on 08/08/2018.
//

#import <Foundation/Foundation.h>

@interface SimpleColor : NSObject

@property (nonatomic) UInt8 r;
@property (nonatomic) UInt8 g;
@property (nonatomic) UInt8 b;

+(SimpleColor *)makeWithR:(UInt8)r g:(UInt8)g b:(UInt8)b;

@end
