function [movie] = makeAccelMovie(accelMatrix)

matSize = size(accelMatrix);
hold on
%for i=1:matSize(1)
for i=80:100
%hold on
axis([-10 10 -10 10 -10 10 -10 10])
xlabel('X')
ylabel('Y')
zlabel('Z')
quiver3(0,0,0,accelMatrix(i,1),accelMatrix(i,2),accelMatrix(i,3))
movie(i) = getframe
%hold off
%clf
end